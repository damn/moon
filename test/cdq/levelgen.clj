(ns cdq.levelgen
  (:require [cdq.db :as db]
            [cdq.db.impl]
            [cdq.files :as files-utils]
            [cdq.graphics.camera :as camera]
            [cdq.graphics.tm-renderer :as tm-renderer]

            [cdq.world-fns.creature-tiles]
            [moon.color :as color]
            [clojure.edn :as edn]
            [moon.application.listener :as listener]
            [moon.backends.lwjgl.application :as application]
            [moon.backends.lwjgl.application.config :as app-config]
            [moon.input :as input]
            [moon.input.keys :as input.keys]
            [moon.graphics.texture :as texture]
            [moon.graphics.orthographic-camera :as orthographic-camera]
            [moon.graphics.g2d.sprite-batch :as sprite-batch]
            [moon.graphics.g2d.texture-region :as texture-region]
            [moon.maps.map-properties :as props]
            [moon.maps.map-layers :as layers]
            [moon.maps.tiled :as tiled-map]
            [moon.maps.tiled.layer :as layer]

            [cdq.ui.stage :as stage] ; This is actually nice because of ctx !
            ; this is not from vis-ui just small extension ....
            ; make userObject @ libgdx PR?
            [moon.scene2d.actor :as actor]
            [moon.scene2d.event :as event]
            [moon.scene2d.utils.change-listener :as change-listener]
            [cdq.ui.table]

            [moon.utils.disposable :as disposable]
            [moon.utils.screen :as screen-utils]
            [moon.utils.viewport :as viewport]
            [moon.utils.viewport.fit-viewport :as fit-viewport]
            [clojure.java.io :as io]
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

(defn- edit-window [skin]
  (let [window (com.badlogic.gdx.scenes.scene2d.ui.Window. "Edit" skin)]
    (cdq.ui.table/add-rows! window (for [level-fn level-fns
                                         :let [on-clicked (fn [actor ctx]
                                                            (let [stage (actor/stage actor)
                                                                  new-ctx (generate-level ctx level-fn)]
                                                              (stage/set-ctx! stage new-ctx)))]]
                                     [{:actor (doto (com.badlogic.gdx.scenes.scene2d.ui.TextButton. (str "Generate " level-fn) skin)
                                                (actor/add-listener!
                                                 (change-listener/create
                                                  (fn [event actor]
                                                    (on-clicked actor (stage/ctx (event/stage event)))))))}]))
    (.pack window)
    window))

(defn create!
  [{:keys [files
           graphics
           input]}]
  (let [skin (com.badlogic.gdx.scenes.scene2d.ui.Skin. (.internal files "uiskin.json")) ; TODO dispose

        ui-viewport (fit-viewport/create 1440 900 (orthographic-camera/create))
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
    (.addActor (:ctx/stage ctx) (edit-window skin))
    ctx))

(defn dispose!
  [{:keys [ctx/sprite-batch
           ctx/tiled-map ]}]
  ; TODO dispose skin?
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
  (app-config/use-glfw-async!)
  (application/create (listener/create
                       {:create (fn []
                                  (reset! state (create! {:files    com.badlogic.gdx.Gdx/files
                                                          :graphics com.badlogic.gdx.Gdx/graphics
                                                          :input    com.badlogic.gdx.Gdx/input})))
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
