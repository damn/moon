(ns moon.levelgen
  (:require [clojure.config :refer [edn-resource]]
            [game.impl.textures]
            [game.impl.db :as db-impl]
            [clojure.gdx :as gdx]
            [clojure.app :as app]
            [clojure.files :as files]
            [clojure.files.file-handle :as file-handle]
            [clojure.graphics.batch :as batch]
            [clojure.graphics.color :as color]
            [clojure.graphics.orthographic-camera :as camera]
            [clojure.graphics.texture :as texture]
            [clojure.input :as input]
            [clojure.input.keys :as input.keys]
            [clojure.scene2d.actor :as actor]
            [clojure.scene2d.event :as event]
            [clojure.scene2d.stage :as stage]
            [clojure.maps.tiled.tiled-map :as tiled-map]
            [clojure.maps.tiled.tiled-map-tile-layer :as layer]
            [clojure.maps.map-layers :as layers]
            [clojure.maps.map-properties :as props]
            [clojure.utils.disposable :as disposable]
            [clojure.utils.viewport :as viewport]
            [moon.creature-tiles]
            [moon.db :as db]))

(def initial-level-fn "world_fns/uf_caves.edn")

(def level-fns
  ["world_fns/vampire.edn"
   "world_fns/uf_caves.edn"
   "world_fns/modules.edn"])

(defn- show-whole-map! [{:keys [ctx/camera
                                ctx/tiled-map]}]
  (camera/set-position! camera
                        [(/ (props/get (tiled-map/properties tiled-map) "width") 2)
                         (/ (props/get (tiled-map/properties tiled-map) "height") 2)])
  (camera/set-zoom! camera
                    (camera/calculate-zoom camera
                                           {:left [0 0]
                                            :top [0 (props/get (tiled-map/properties tiled-map) "height")]
                                            :right [(props/get (tiled-map/properties tiled-map) "width") 0]
                                            :bottom [0 0]})))

(def tile-size 48)

(defn- generate-level
  [{:keys [ctx/db
           ctx/textures
           ctx/tiled-map] :as ctx} level-fn]
  (when tiled-map
    (tiled-map/dispose! tiled-map))
  (let [level (let [[f params] (edn-resource level-fn)]
                (f
                 (assoc params
                        :level/creature-properties (moon.creature-tiles/prepare
                                                    (db/all-raw db :properties/creatures)
                                                    (fn [{:keys [image/file image/bounds]}]
                                                      (assert file)
                                                      (assert (contains? textures file))
                                                      (let [texture (get textures file)]
                                                        (if-let [[x y w h] bounds]
                                                          (texture/region texture x y w h)
                                                          (texture/region texture)))))
                        :textures textures)))
        tiled-map (:tiled-map level)
        ctx (assoc ctx :ctx/tiled-map tiled-map)]
    (assert tiled-map)
    (-> tiled-map
        tiled-map/layers
        (layers/get "creatures")
        (layer/set-visible! true))
    (show-whole-map! ctx)
    ctx))

(defn- edit-window [skin]
  {:type :ui/window
   :title "Edit"
   :skin skin
   :table/rows (for [level-fn level-fns
                     :let [on-clicked (fn [actor ctx]
                                        (let [stage (actor/stage actor)
                                              new-ctx (generate-level ctx level-fn)]
                                          (stage/set-ctx! stage new-ctx)))]]
                 [{:actor (actor/create
                           {:type :ui/text-button
                            :text (str "Generate " level-fn)
                            :skin skin
                            :actor/listeners {:listener/change (fn [event actor]
                                                                 (on-clicked actor (:stage/ctx (event/stage event))))}})}])})

(defn create!
  [app]
  (let [files (app/files app)
        graphics (app/graphics app)
        input (app/input app)
        skin (file-handle/skin (files/internal files "uiskin.json"))
        ui-viewport (gdx/fit-viewport 1440 900)
        sprite-batch (gdx/sprite-batch)
        stage (gdx/stage ui-viewport sprite-batch)
        _  (input/set-processor! input stage)
        tile-size 48
        world-unit-scale (float (/ tile-size))
        ctx {:ctx/stage stage
             :ctx/files files}
        ctx (assoc ctx :ctx/db (db-impl/create))
        ctx (assoc ctx :ctx/textures (game.impl.textures/create files))
        world-viewport (let [world-width  (* 1440 world-unit-scale)
                             world-height (* 900  world-unit-scale)]
                         (gdx/fit-viewport world-width
                                           world-height
                                           (gdx/orthographic-camera
                                            {:y-down? false
                                             :world-width world-width
                                             :world-height world-height})))
        ctx (assoc ctx
                   :ctx/input input
                   :ctx/world-viewport world-viewport
                   :ctx/ui-viewport ui-viewport
                   :ctx/camera (:viewport/camera world-viewport)
                   :ctx/color-setter (constantly (color/float-bits [1 1 1 1]))
                   :ctx/zoom-speed 0.1
                   :ctx/camera-movement-speed 1
                   :ctx/sprite-batch sprite-batch
                   :ctx/world-unit-scale world-unit-scale)
        ctx (generate-level ctx initial-level-fn)]
    (stage/add-actor! (:ctx/stage ctx) (edit-window skin))
    ctx))

(defn dispose!
  [{:keys [ctx/skin
           ctx/sprite-batch
           ctx/tiled-map]}]
  (disposable/dispose! skin)
  (disposable/dispose! sprite-batch)
  (disposable/dispose! tiled-map))

(defn- draw-tiled-map! [{:keys [ctx/sprite-batch
                                ctx/color-setter
                                ctx/tiled-map
                                ctx/world-unit-scale
                                ctx/world-viewport]}]
  (batch/draw-tiled-map! sprite-batch
                         world-unit-scale
                         (:viewport/camera world-viewport)
                         tiled-map
                         color-setter))

(defn- camera-movement-controls! [{:keys [ctx/input
                                          ctx/camera
                                          ctx/camera-movement-speed]}]
  (let [apply-position (fn [idx f]
                         (camera/set-position! camera
                                               (update (camera/position camera)
                                                       idx
                                                       #(f % camera-movement-speed))))]
    (if (input/key-pressed? input input.keys/left)  (apply-position 0 -))
    (if (input/key-pressed? input input.keys/right) (apply-position 0 +))
    (if (input/key-pressed? input input.keys/up)    (apply-position 1 +))
    (if (input/key-pressed? input input.keys/down)  (apply-position 1 -))))

(defn- camera-zoom-controls! [{:keys [ctx/input
                                      ctx/camera
                                      ctx/zoom-speed]}]
  (when (input/key-pressed? input input.keys/minus)  (camera/inc-zoom! camera zoom-speed))
  (when (input/key-pressed? input input.keys/equals) (camera/inc-zoom! camera (- zoom-speed))))

(defn render!
  [{:keys [ctx/stage]
    :as ctx}]
  (let [ctx (if-let [new-ctx (:stage/ctx stage)]
              new-ctx
              ctx)]
    (gdx/clear-screen! [0 0 0 0])
    (draw-tiled-map! ctx)
    (camera-zoom-controls! ctx)
    (camera-movement-controls! ctx)
    (stage/set-ctx! stage ctx)
    (stage/act! stage)
    (stage/draw! stage)
    (:stage/ctx stage)))

(defn resize!
  [{:keys [ctx/ui-viewport
           ctx/world-viewport]}
   width height]
  (viewport/update! ui-viewport    width height true)
  (viewport/update! world-viewport width height false))

(def state (atom nil))

(defn -main []
  (gdx/application!
   {:title "Levelgen Test"
    :windowed-mode {:width 1440
                    :height 900}
    :foreground-fps 60
    :create! (fn [app]
               (reset! state (create! app)))

    :dispose! (fn []
                (dispose! @state))

    :render! (fn []
               (swap! state render!))

    :resize! (fn [width height]
               (resize! @state width height))

    :pause! (fn [])

    :resume! (fn [])}))
