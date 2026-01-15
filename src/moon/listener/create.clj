(ns moon.listener.create
  (:require [clojure.edn :as edn]
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
           (com.badlogic.gdx.graphics Pixmap
                                      Texture
                                      OrthographicCamera)
           (com.badlogic.gdx.scenes.scene2d.ui Skin)
           (com.badlogic.gdx.utils.viewport FitViewport)
           (moon Stage)))

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

(q/defrecord Context [])

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
  [^Application app config]
  ; TODO postcondition validate?
  (let [db ((requiring-resolve (:db-impl config)))
        graphics ((requiring-resolve (:graphics-impl config)) (.getGraphics app) (.getFiles app) (:graphics config))
        ui-viewport (FitViewport. (:width  (:ui-viewport config))
                                  (:height (:ui-viewport config))
                                  (OrthographicCamera.))
        stage (Stage. ui-viewport (:graphics/batch graphics))
        skin (create-skin (.internal (.getFiles app) "uiskin.json"))
        textures (into {} (for [path (files-utils/search (.getFiles app) (:texture-folder config))]
                            [path (Texture. ^String path)]))
        ctx (merge (map->Context {})
                   {:ctx/audio (load-sounds (.getAudio app) (.getFiles app) (:audio config))
                    :ctx/db db
                    :ctx/graphics graphics
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
             :ctx/world-viewport (let [world-unit-scale (:graphics/world-unit-scale graphics)
                                       world-width  (* (:width  (:world-viewport config)) world-unit-scale)
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
