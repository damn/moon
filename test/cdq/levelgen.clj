(ns cdq.levelgen
  (:require [cdq.db :as db]
            [cdq.db.impl]
            [cdq.files :as files-utils]
            [cdq.graphics.camera :as camera]
            [cdq.graphics.tm-renderer :as tm-renderer]

            [cdq.ui.stage :as stage]

            [cdq.world-fns.creature-tiles]
            [clojure.color :as color]
            [clojure.edn :as edn]
            [clojure.gdx :as gdx]
            [clojure.gdx.application.listener :as listener]
            [clojure.gdx.backends.lwjgl.application :as application]
            [clojure.gdx.backends.lwjgl.application.config :as app-config]
            [clojure.gdx.input :as input]
            [clojure.gdx.input.keys :as input.keys]
            [clojure.gdx.graphics.texture :as texture]
            [clojure.gdx.graphics.orthographic-camera :as orthographic-camera]
            [clojure.gdx.graphics.g2d.sprite-batch :as sprite-batch]
            [clojure.gdx.graphics.g2d.texture-region :as texture-region]
            [clojure.gdx.maps.map-properties :as props]
            [clojure.gdx.maps.map-layers :as layers]
            [clojure.gdx.maps.tiled :as tiled-map]
            [clojure.gdx.maps.tiled.layer :as layer]
            [clojure.gdx.scene2d.actor :as actor]
            [clojure.gdx.utils.disposable :as disposable]
            [clojure.gdx.utils.screen :as screen-utils]
            [clojure.gdx.utils.viewport :as viewport]
            [clojure.gdx.utils.viewport.fit-viewport :as fit-viewport]
            [clojure.java.io :as io]
            [clojure.lwjgl.system.configuration :as lwjgl-config]

            [cdq.ui.skin :as vis-ui]
            [cdq.ui.text-button :as text-button]
            [cdq.ui.window :as window]

            ))

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
                                           :left [0 0]
                                           :top [0 (props/get (tiled-map/properties tiled-map) "height")]
                                           :right [(props/get (tiled-map/properties tiled-map) "width") 0]
                                           :bottom [0 0])))

(def tile-size 48)

(defn- generate-level [{:keys [ctx/db
                               ctx/textures
                               ctx/tiled-map] :as ctx} level-fn]
  (when tiled-map
    (disposable/dispose! tiled-map))
  (let [level (let [[f params] (-> level-fn
                                   io/resource
                                   slurp
                                   edn/read-string)]
                ((requiring-resolve f)
                 (assoc params
                        :level/creature-properties (cdq.world-fns.creature-tiles/prepare
                                                    (db/all-raw db :properties/creatures)
                                                    (fn [{:keys [image/file image/bounds]}]
                                                      (assert file)
                                                      (assert (contains? textures file))
                                                      (let [texture (get textures file)]
                                                        (if bounds
                                                          (texture-region/create texture bounds)
                                                          (texture-region/create texture)))))
                        :textures textures)))
        tiled-map (:tiled-map level)
        ctx (assoc ctx :ctx/tiled-map tiled-map)]
    (layer/set-visible! (layers/get (tiled-map/layers tiled-map) "creatures") true)
    (show-whole-map! ctx)
    ctx))

(defn- edit-window []
  (window/create
   {:title "Edit"
    :cell-defaults {:pad 10}
    :rows (for [level-fn level-fns]
            [{:actor (text-button/create
                      {:text (str "Generate " level-fn)
                       :on-clicked (fn [actor ctx]
                                     (let [stage (actor/stage actor)
                                           new-ctx (generate-level ctx level-fn)]
                                       (stage/set-ctx! stage new-ctx)))})}])
    :pack? true}))

(defn create!
  [{:keys [files
           graphics
           input]}]
  (vis-ui/load! {:skin-scale :x1})
  ; skin = new Skin(Gdx.files.internal("data/uiskin.json"));
  (let [ui-viewport (fit-viewport/create 1440 900 (orthographic-camera/create))
        sprite-batch (sprite-batch/create)
        stage (stage/create ui-viewport sprite-batch nil)
        _  (input/set-processor! input stage)
        tile-size 48
        world-unit-scale (float (/ tile-size))
        ctx {:ctx/stage stage}
        ctx (-> ctx
                (assoc :ctx/db (cdq.db.impl/create)))
        world-viewport (let [world-width  (* 1440 world-unit-scale)
                             world-height (* 900  world-unit-scale)]
                         (fit-viewport/create world-width
                                              world-height
                                              (doto (orthographic-camera/create)
                                                (orthographic-camera/set-to-ortho! false world-width world-height))))
        ctx (assoc ctx
                   :ctx/input input
                   :ctx/world-viewport world-viewport
                   :ctx/ui-viewport ui-viewport
                   :ctx/textures (into {} (for [path (files-utils/search files
                                                                         {:folder "resources/"
                                                                          :extensions #{"png" "bmp"}})]
                                            [path (texture/create path)]))
                   :ctx/camera (viewport/camera world-viewport)
                   :ctx/color-setter (constantly [1 1 1 1])
                   :ctx/zoom-speed 0.1
                   :ctx/camera-movement-speed 1
                   :ctx/sprite-batch sprite-batch
                   :ctx/tiled-map-renderer (tm-renderer/create world-unit-scale sprite-batch))
        ctx (generate-level ctx initial-level-fn)]
    (.addActor (:ctx/stage ctx) (edit-window))
    ctx))

(defn dispose!
  [{:keys [ctx/sprite-batch
           ctx/tiled-map ]}]
  (vis-ui/dispose!)
  (disposable/dispose! sprite-batch)
  (disposable/dispose! tiled-map))

(defn- draw-tiled-map! [{:keys [ctx/color-setter
                                ctx/tiled-map
                                ctx/tiled-map-renderer
                                ctx/world-viewport]}]
  (tm-renderer/draw! tiled-map-renderer
                     world-viewport
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
  (let [ctx (if-let [new-ctx (stage/ctx stage)]
              new-ctx
              ctx)]
    (screen-utils/clear! color/black)
    (draw-tiled-map! ctx)
    (camera-zoom-controls! ctx)
    (camera-movement-controls! ctx)
    (stage/set-ctx! stage ctx)
    (stage/act! stage)
    (stage/draw! stage)
    (stage/ctx stage)))

(defn resize!
  [{:keys [ctx/ui-viewport
           ctx/world-viewport]}
   width height]
  (viewport/update! ui-viewport    width height {:center? true})
  (viewport/update! world-viewport width height {:center? false}))

(def state (atom nil))

(defn -main []
  (lwjgl-config/set-glfw-library-name! "glfw_async")
  (application/create (listener/create
                       {:create (fn []
                                  (reset! state (create! (gdx/state))))
                        :dispose (fn []
                                   (dispose! @state))
                        :render (fn []
                                  (swap! state render!))
                        :resize (fn [width height]
                                  (resize! @state width height))
                        :pause (fn [])
                        :resume (fn [])})
                      (app-config/create
                       {:title "Levelgen Test"
                        :window {:width 1440
                                 :height 900}
                        :fps 60})))
