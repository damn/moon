(ns moon.listener.create
  (:require [clj.api.space.earlygrey.shape-drawer :as sd]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [moon.ctx :as ctx]
            [moon.db :as db]
            [moon.files :as files-utils]
            [moon.textures :as textures]
            [moon.tx-handler :as tx-handler]
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

(defn create-default-font [{:keys [ctx/files] :as ctx} {:keys [path params]}]
  (assoc ctx :ctx/default-font (generate-font (.internal files path)
                                              params)))

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

(def reaction-txs-fn-map
  (update-vals '{
                 :tx/sound                    moon.reaction-txs.sound/do!
                 :tx/toggle-inventory-visible moon.reaction-txs.toggle-inventory-visible/do!
                 :tx/show-message             moon.reaction-txs.show-message/do!
                 :tx/show-modal               moon.reaction-txs.show-modal/do!
                 :tx/set-item                 moon.reaction-txs.set-item/do!
                 :tx/remove-item              moon.reaction-txs.remove-item/do!
                 :tx/add-skill                moon.reaction-txs.add-skill/do!
                 }
               requiring-resolve))

(def txs-fn-map
  (update-vals '{
                 :tx/state-exit               moon.tx.state-exit/do!
                 :tx/audiovisual              moon.tx.audiovisual/do!
                 :tx/assoc                    moon.tx.assoc/do!
                 :tx/assoc-in                 moon.tx.assoc-in/do!
                 :tx/dissoc                   moon.tx.dissoc/do!
                 :tx/update                   moon.tx.update/do!
                 :tx/mark-destroyed           moon.tx.mark-destroyed/do!
                 :tx/set-cooldown             moon.tx.set-cooldown/do!
                 :tx/add-text-effect          moon.tx.add-text-effect/do!
                 :tx/add-skill                moon.tx.add-skill/do!
                 :tx/set-item                 moon.tx.set-item/do!
                 :tx/remove-item              moon.tx.remove-item/do!
                 :tx/pickup-item              moon.tx.pickup-item/do!
                 :tx/event                    moon.tx.event/do!
                 :tx/state-enter              moon.tx.state-enter/do!
                 :tx/effect                   moon.tx.effect/do!
                 :tx/spawn-alert              moon.tx.spawn-alert/do!
                 :tx/spawn-line               moon.tx.spawn-line/do!
                 :tx/move-entity              moon.tx.move-entity/do!
                 :tx/spawn-projectile         moon.tx.spawn-projectile/do!
                 :tx/spawn-effect             moon.tx.spawn-effect/do!
                 :tx/spawn-item               moon.tx.spawn-item/do!
                 :tx/spawn-creature           moon.tx.spawn-creature/do!
                 :tx/spawn-entity             moon.tx.spawn-entity/do!
                 :tx/sound                    moon.tx.nothing/do!
                 :tx/toggle-inventory-visible moon.tx.nothing/do!
                 :tx/show-message             moon.tx.nothing/do!
                 :tx/show-modal               moon.tx.nothing/do!
                 }
               requiring-resolve))

(defn- reduce-actions!
  [txs-fn-map ctx txs]
  (loop [ctx ctx
         txs txs]
    (if (empty? txs)
      ctx
      (let [[k & params :as tx] (first txs)]
        (if tx
          (let [_ (assert (vector? tx))
                f (get txs-fn-map k)
                new-ctx (try
                         (if (nil? f)
                           ctx
                           (apply f ctx params))
                         (catch Throwable t
                           (throw (ex-info "Error handling tx"
                                           {:tx tx}
                                           t))))]
            (recur new-ctx
                   (rest txs)))
          (recur ctx
                 (rest txs)))))))

(q/defrecord Context []
  moon.ctx/TransactionHandler
  (handle! [ctx txs]
    (let [handled-txs (try (tx-handler/actions! txs-fn-map ctx txs)
                           (catch Throwable t
                             (throw (ex-info "Error handling txs"
                                             {:txs txs} t))))]
      (reduce-actions! reaction-txs-fn-map
                       ctx
                       handled-txs)))

  moon.ctx/Graphics
  (draw! [ctx draws]
    (doseq [{k 0 :as component} draws
            :when component]
      (apply (get draw-fns k) ctx (rest component)))))

(defn- load-sounds*
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

(defn load-sounds
  [{:keys [ctx/audio
           ctx/files]
    :as ctx}
   config]
  (assoc ctx :ctx/audio (load-sounds* audio files config)))

(defn- create-cursor [files graphics path [hotspot-x hotspot-y]]
  (let [pixmap (Pixmap. (.internal files path))
        cursor (Graphics/.newCursor graphics pixmap hotspot-x hotspot-y)]
    (.dispose pixmap)
    cursor))

(defn- def-colors! [ctx colors]
  (doseq [[name [r g b a]] colors]
    (Colors/put name (Color. r g b a)))
  ctx)

(defn- create-db [ctx db-impl]
  (assoc ctx :ctx/db (db-impl)))

(defn- create-ui-viewport [ctx {:keys [width height]}]
  (assoc ctx :ctx/ui-viewport (FitViewport. width height (OrthographicCamera.))))

(defn- create-batch [ctx]
  (assoc ctx :ctx/batch (SpriteBatch.)))

(defn- create-stage [{:keys [ctx/batch
                             ctx/ui-viewport]
                      :as ctx}]
  (assoc ctx :ctx/stage (Stage. ui-viewport batch)))

(defn- create-skin [{:keys [ctx/files] :as ctx} path]
  (assoc ctx :ctx/skin (let [skin (Skin. (.internal files path))]
                         (set! (.markupEnabled (-> skin (.getFont "default-font") .getData))
                               true)
                         skin)))

(defn create-textures [{:keys [ctx/files] :as ctx} folder]
  (assoc ctx :ctx/textures
         (into {} (for [path (files-utils/search files folder)]
                    [path (Texture. ^String path)]))))

(defn shape-drawer-texture [ctx]
  (assoc ctx :ctx/shape-drawer-texture
         (let [pixmap (doto (Pixmap. 1 1 Pixmap$Format/RGBA8888)
                        (.setColor 1 1 1 1)
                        (.drawPixel 0 0))
               texture (Texture. pixmap)]
           (.dispose pixmap)
           texture)))

(defn world-unit-scale [ctx tile-size]
  (assoc ctx :ctx/world-unit-scale (float (/ tile-size))))

(defn create-shape-drawer
  [{:keys [ctx/batch
           ctx/shape-drawer-texture]
    :as ctx}]
  (assoc ctx :ctx/shape-drawer (sd/create batch (TextureRegion. shape-drawer-texture 1 0 1 1))))

(defn set-input-processor [{:keys [ctx/input
                                   ctx/stage]
                            :as ctx}]
  (Input/.setInputProcessor input stage)
  ctx)

(defn add-stage-actors
  [{:keys [ctx/stage] :as ctx} actor-fns]
  (doseq [actor (map (fn [[sym & params]] (apply (requiring-resolve sym) ctx params)) actor-fns)]
    (.addActor stage actor))
  ctx)

(defn create-world
  [{:keys [ctx/db
           ctx/textures]
    :as ctx}
   config]
  (let [world-fn-result (call-world-fn (:world config)
                                       (db/all-raw db :properties/creatures)
                                       textures)]
    (assoc ctx :ctx/world ((requiring-resolve (:world-impl config))
                           world-params
                           world-fn-result))))

(defn spawn-player-entity!
  [{:keys [ctx/db
           ctx/world]
    :as ctx}]
  (ctx/handle! ctx
               [[:tx/spawn-creature (let [{:keys [creature-id
                                                  components]} (:world/player-components world)]
                                      {:position (mapv (partial + 0.5) (:world/start-position world))
                                       :creature-property (db/build db creature-id)
                                       :components components})]])
  (let [eid (get @(:world/entity-ids world) 1)]
    (assert (:entity/player? @eid))
    (assoc ctx :ctx/player-eid eid)))

(defn add-leftout-ctxs [ctx]
  (assoc ctx
         :ctx/mouseover-eid nil
         :ctx/paused? false ; is set before checked ... this setting here is irrelevant
         :ctx/world-mouse-position nil
         :ctx/ui-mouse-position nil))

(defn spawn-enemies!
  [{:keys [ctx/db
           ctx/world]
    :as ctx}]
  (ctx/handle!
   ctx
   (for [[position creature-id] (tiled-map/spawn-positions (:world/tiled-map world))]
     [:tx/spawn-creature {:position (mapv (partial + 0.5) position)
                          :creature-property (db/build db (keyword creature-id))
                          :components (:world/enemy-components world)}]))
  ctx)

(defn create-world-viewport
  [{:keys [ctx/world-unit-scale]
    :as ctx}
   {:keys [width height]}]
  (assoc ctx :ctx/world-viewport
         (let [world-width  (* width world-unit-scale)
               world-height (* height world-unit-scale)]
           (FitViewport. world-width
                         world-height
                         (doto (OrthographicCamera.)
                           (.setToOrtho false world-width world-height))))))

(defn create-cursors
  [{:keys [ctx/files
           ctx/graphics]
    :as ctx}
   {:keys [data path-format]}]
  (assoc ctx :ctx/cursors
         (update-vals data
                      (fn [[path hotspot]]
                        (create-cursor files
                                       graphics
                                       (format path-format path)
                                       hotspot)))))

(defn do!
  [^Application app
   {:keys [colors
           default-font
           tile-size]
    :as config}]
  (reduce (fn [ctx [f & params]]
            (apply f ctx params))
          (merge (map->Context {})
                 {:ctx/audio (.getAudio app)
                  :ctx/graphics (.getGraphics app)
                  :ctx/files (.getFiles app)
                  :ctx/input (.getInput app)
                  :ctx/unit-scale (atom 1)})
          [
           [def-colors! colors]
           [create-cursors (:cursors config)]
           [create-db (requiring-resolve (:db-impl config))]
           [create-ui-viewport (:ui-viewport config)]
           [create-batch]
           [create-stage]
           [set-input-processor]
           [create-skin "uiskin.json"]
           [create-textures (:texture-folder config)]
           [shape-drawer-texture]
           [world-unit-scale tile-size]
           [create-world-viewport (:world-viewport config)]
           [load-sounds (:audio config)]
           [create-default-font default-font]
           [create-shape-drawer]
           [set-input-processor]
           [add-stage-actors (:ui/actors config)]
           [create-world config]
           [spawn-player-entity!]
           [spawn-enemies!]
           [add-leftout-ctxs]
           ]))
