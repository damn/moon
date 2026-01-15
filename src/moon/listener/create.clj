(ns moon.listener.create
  (:require [clj.api.space.earlygrey.shape-drawer :as sd]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [moon.ctx :as ctx]
            [moon.db :as db]
            [moon.files :as files-utils]
            [moon.textures :as textures]
            [moon.world :as world]
            [moon.world-fns.creature-tiles]
            [moon.world.tiled-map :as tiled-map]
            [qrecord.core :as q])
  (:import (com.badlogic.gdx Application
                             Audio
                             Files
                             Graphics
                             Input)
           (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics Color
                                      Colors
                                      Pixmap
                                      Pixmap$Format
                                      Texture
                                      Texture$TextureFilter
                                      OrthographicCamera)
           (com.badlogic.gdx.graphics.g2d SpriteBatch
                                          TextureRegion)
           (com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator
                                                   FreeTypeFontGenerator$FreeTypeFontParameter)
           (com.badlogic.gdx.scenes.scene2d.ui Skin)
           (com.badlogic.gdx.utils.viewport FitViewport)
           (moon Stage)))

(defn- generate-font
  [file-handle {:keys [size
                       quality-scaling
                       enable-markup?
                       use-integer-positions?]}]
  (let [generator (FreeTypeFontGenerator. file-handle)
        font (.generateFont generator
                            (let [params (FreeTypeFontGenerator$FreeTypeFontParameter.)]
                              (set! (.size params) (* size quality-scaling))
                              (set! (.minFilter params) Texture$TextureFilter/Linear)
                              (set! (.magFilter params) Texture$TextureFilter/Linear)
                              params))]
    (.dispose generator)
    (.setScale (.getData font) (/ quality-scaling))
    (set! (.markupEnabled (.getData font)) enable-markup?)
    (.setUseIntegerPositions font use-integer-positions?)
    font))

(defn- call-world-fn
  [world-fn creature-properties textures]
  (let [[f params] (->> world-fn
                        io/resource
                        slurp
                        edn/read-string)]
    ((requiring-resolve f)
     (assoc params
            :level/creature-properties (moon.world-fns.creature-tiles/prepare creature-properties
                                                                             #(textures/texture-region textures %))
            :textures textures))))

(def ^:private world-params
  {
   :content-grid-cell-size 16
   :world/factions-iterations {:good 15 :evil 5}
   :world/max-delta 0.04
   :world/minimum-size 0.39
   :world/z-orders [:z-order/on-ground
                    :z-order/ground
                    :z-order/flying
                    :z-order/effect]
   :world/enemy-components {:entity/fsm {:fsm :fsms/npc
                                         :initial-state :npc-sleeping}
                            :entity/faction :evil}
   :world/player-components {:creature-id :creatures/vampire
                             :components {:entity/fsm {:fsm :fsms/player
                                                       :initial-state :player-idle}
                                          :entity/faction :good
                                          :entity/player? true
                                          :entity/free-skill-points 3
                                          :entity/clickable {:type :clickable/player}
                                          :entity/click-distance-tiles 1.5}}
   })

(def draw-fns
  (update-vals '{:draw/arc              moon.draw.arc/do!
                 :draw/circle           moon.draw.circle/do!
                 :draw/ellipse          moon.draw.ellipse/do!
                 :draw/filled-circle    moon.draw.filled-circle/do!
                 :draw/filled-ellipse   moon.draw.filled-ellipse/do!
                 :draw/filled-rectangle moon.draw.filled-rectangle/do!
                 :draw/grid             moon.draw.grid/do!
                 :draw/line             moon.draw.line/do!
                 :draw/rectangle        moon.draw.rectangle/do!
                 :draw/sector           moon.draw.sector/do!
                 :draw/text             moon.draw.text/do!
                 :draw/texture-region   moon.draw.texture-region/do!
                 :draw/with-line-width  moon.draw.with-line-width/do!}
               requiring-resolve))

(q/defrecord Context []
  moon.ctx/Graphics
  (draw! [ctx draws]
    (doseq [{k 0 :as component} draws
            :when component]
      (apply (get draw-fns k) ctx (rest component)))))

(defn- load-sounds
  [audio files {:keys [sound-names path-format]}]
  (let [sound-name->file-handle (into {}
                                      (for [sound-name (->> sound-names io/resource slurp edn/read-string)
                                            :let [path (format path-format sound-name)]]
                                        [sound-name
                                         (Files/.internal files path)]))]
    (into {}
          (for [[sound-name file-handle] sound-name->file-handle]
            [sound-name
             (Audio/.newSound audio file-handle)]))))

(defn- create-skin [^FileHandle file-handle]
  (let [skin (Skin. file-handle)]
    (set! (.markupEnabled (-> skin (.getFont "default-font") .getData))
          true)
    skin))

(defn- create-cursor [files graphics path [hotspot-x hotspot-y]]
  (let [pixmap (Pixmap. (.internal files path))
        cursor (Graphics/.newCursor graphics pixmap hotspot-x hotspot-y)]
    (.dispose pixmap)
    cursor))

; Idea:
; Assert what is there before passing ctx somwehere (can do select-keys)
; e.g. what does add actors need? .. textures/etc.
(defn do!
  [^Application app
   {:keys [colors
           default-font
           tile-size]
    :as config}]
  ; TODO postcondition validate?
  (doseq [[name [r g b a]] colors] ; remove out
    (Colors/put name (Color. r g b a)))
  (let [db ((requiring-resolve (:db-impl config)))
        ui-viewport (FitViewport. (:width  (:ui-viewport config))
                                  (:height (:ui-viewport config))
                                  (OrthographicCamera.))
        batch (SpriteBatch.)
        stage (Stage. ui-viewport batch)
        skin (create-skin (.internal (.getFiles app) "uiskin.json"))
        textures (into {} (for [path (files-utils/search (.getFiles app) (:texture-folder config))]
                            [path (Texture. ^String path)]))
        shape-drawer-texture (let [pixmap (doto (Pixmap. 1 1 Pixmap$Format/RGBA8888)
                                            (.setColor 1 1 1 1)
                                            (.drawPixel 0 0))
                                   texture (Texture. pixmap)]
                               (.dispose pixmap)
                               texture)
        world-unit-scale (float (/ tile-size))
        ctx (merge (map->Context {})
                   {:ctx/audio (load-sounds (.getAudio app) (.getFiles app) (:audio config))
                    :ctx/db db
                    :ctx/graphics (.getGraphics app)

                    :ctx/default-font (generate-font (.internal (.getFiles app) (:path default-font))
                                                     (:params default-font))
                    :ctx/batch batch
                    :ctx/shape-drawer-texture shape-drawer-texture
                    :ctx/shape-drawer (sd/create batch (TextureRegion. shape-drawer-texture 1 0 1 1))
                    :ctx/unit-scale (atom 1)
                    :ctx/world-unit-scale world-unit-scale

                    :ctx/input (.getInput app)
                    :ctx/stage stage
                    :ctx/skin skin
                    :ctx/textures textures
                    :ctx/ui-viewport ui-viewport
                    })]
    (Input/.setInputProcessor (.getInput app) stage)
    (doseq [actor (map (fn [[sym & params]] (apply (requiring-resolve sym) ctx params)) (:ui/actors config))]
      (.addActor stage actor))
    (let [world-fn-result (call-world-fn (:world config)
                                         (db/all-raw db :properties/creatures)
                                         textures)
          world ((requiring-resolve (:world-impl config)) world-params world-fn-result)
          ctx (assoc ctx :ctx/world world)
          _ (ctx/handle! ctx
                         [[:tx/spawn-creature (let [{:keys [creature-id
                                                            components]} (:world/player-components world)]
                                                {:position (mapv (partial + 0.5) (:world/start-position world))
                                                 :creature-property (db/build db creature-id)
                                                 :components components})]])
          ctx (let [eid (get @(:world/entity-ids world) 1)]
                (assert (:entity/player? @eid))
                (assoc ctx :ctx/player-eid eid))]
      (ctx/handle!
       ctx
       (for [[position creature-id] (tiled-map/spawn-positions (:world/tiled-map world))]
         [:tx/spawn-creature {:position (mapv (partial + 0.5) position)
                              :creature-property (db/build db (keyword creature-id))
                              :components (:world/enemy-components world)}]))

      (assoc ctx
             :ctx/mouseover-eid nil
             :ctx/paused? false ; is set before checked ... this setting here is irrelevant
             :ctx/world-mouse-position nil
             :ctx/ui-mouse-position nil
             :ctx/world-viewport (let [world-width  (* (:width  (:world-viewport config)) world-unit-scale)
                                       world-height (* (:height (:world-viewport config)) world-unit-scale)]
                                   (FitViewport. world-width
                                                 world-height
                                                 (doto (OrthographicCamera.)
                                                   (.setToOrtho false world-width world-height))))
             :ctx/cursors (update-vals (:data (:cursors config))
                                       (fn [[path hotspot]]
                                         (create-cursor (.getFiles app)
                                                        (.getGraphics app)
                                                        (format (:path-format (:cursors config)) path)
                                                        hotspot)))
             ))))
