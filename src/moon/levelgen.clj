(ns moon.levelgen
  (:require [moon.db :as db]
            [gdx.camera.orthographic :as orthographic-camera]
            [moon.level.modules :as modules]
            [moon.level.tmx :as tmx]
            [moon.level.uf-caves :as uf-caves]
            [moon.schema.register-methods]
            [moon.textures :as textures]
            [gdx.stage :as stage]
            [gdx.actor.group.widget.table :as table]
            [gdx.tiled-map :as moon-tiled-map]
            [gdx.files :as files]
            [gdx.graphics :as graphics]
            [gdx.color :as color]
            [gdx.sprite-batch :as sprite-batch]
            [gdx.gl20 :as gl20]
            [gdx.input :as input]
            [gdx.map-layers :as map-layers]
            [gdx.tiled-map-tile-layer :as tiled-map-tile-layer]
            [gdx.actor :as actor]
            [gdx.skin :as skin]
            [gdx.actor.group.widget.table.button.text :as text-button]
            [gdx.actor.group.widget.table.window :as window]
            [gdx.change-listener :as change-listener]
            [gdx.disposable :as disposable]
            [gdx.viewport.fit :as fit-viewport]
            [gdx.viewport :as viewport]
            [gdx.application :as application]
            [gdx.application.lwjgl :as lwjgl-application]))

(def state (atom nil))

(def ^:private config
  {:initial-level-fn uf-caves/create
   :level-fns [["Vampire" tmx/vampire]
               ["UF Caves" uf-caves/create]
               ["Modules" modules/create]]
   :ui-viewport-width 1440
   :ui-viewport-height 900
   :world-viewport-width 1440
   :world-viewport-height 900
   :tile-size 48
   :ui-skin-path "skin/uiskin.json"
   :textures-config {:folder "resources/"
                     :extensions #{"png" "bmp"}}
   :zoom-speed 0.1
   :camera-movement-speed 1})

(defn- generate-level
  [{:keys [ctx/camera
           ctx/db
           ctx/textures]
    :as ctx}
   level-fn]
  (let [level (level-fn
               {:level/creature-properties (moon-tiled-map/prepare-creature-tiles
                                            (db/all-raw db :properties/creatures)
                                            #(textures/texture-region textures %))
                :textures textures})
        tiled-map (:tiled-map level)
        ctx (assoc ctx :ctx/tiled-map tiled-map)]
    (assert tiled-map)
    (-> tiled-map
        moon-tiled-map/get-layers
        (map-layers/get "creatures")
        (tiled-map-tile-layer/set-visible! true))
    (orthographic-camera/set-position! camera [(/ (moon-tiled-map/get-property tiled-map "width") 2)
                           (/ (moon-tiled-map/get-property tiled-map "height") 2)])
    (orthographic-camera/zoom-to-rect camera {:left [0 0]
                            :top [0 (moon-tiled-map/get-property tiled-map "height")]
                            :right [(moon-tiled-map/get-property tiled-map "width") 0]
                            :bottom [0 0]})
    ctx))

(defn create
  [state config application]
  (let [files (application/get-files application)
        input (application/get-input application)
        graphics (application/get-graphics application)
        sprite-batch (sprite-batch/create)
        ui-viewport (fit-viewport/create (:ui-viewport-width config)
                                      (:ui-viewport-height config))
        world-unit-scale (float (/ (:tile-size config)))
        stage (stage/create ui-viewport sprite-batch)
        skin (skin/create (files/internal files (:ui-skin-path config)))
        world-viewport (let [world-width (* (:world-viewport-width config) world-unit-scale)
                             world-height (* (:world-viewport-height config) world-unit-scale)]
                         (fit-viewport/create world-width
                                           world-height
                                           (doto (orthographic-camera/new)
                                             (orthographic-camera/set-to-ortho! false world-width world-height))))
        ctx {:ctx/input input
             :ctx/graphics graphics
             :ctx/stage stage
             :ctx/zoom-speed (:zoom-speed config)
             :ctx/camera-movement-speed (:camera-movement-speed config)
             :ctx/world-unit-scale world-unit-scale
             :ctx/db (db/create)
             :ctx/textures (textures/create files (:textures-config config))
             :ctx/sprite-batch sprite-batch
             :ctx/skin skin
             :ctx/world-viewport world-viewport
             :ctx/camera (viewport/get-camera world-viewport)}
        ctx (generate-level ctx (:initial-level-fn config))]
    (input/set-processor! input stage)
    (stage/add-actor! (:ctx/stage ctx)
                    (window/create {:title "Edit"
                                    :skin skin
                                    :table/rows (for [[label level-fn] (:level-fns config)
                                                      :let [on-click #(do
                                                                        (disposable/dispose! (:ctx/tiled-map %))
                                                                        (generate-level % level-fn))]]
                                                   [{:actor (doto (text-button/create (str "Generate " label) skin)
                                                                  (actor/add-listener!
                                                                   (change-listener/create
                                                                    (fn [_event _actor]
                                                                      (swap! state on-click)))))}])}))
    ctx))

(defn dispose
  [{:keys [ctx/skin
           ctx/sprite-batch
           ctx/textures
           ctx/tiled-map]}]
  (disposable/dispose! sprite-batch)
  (disposable/dispose! skin)
  (run! disposable/dispose! (vals textures))
  (disposable/dispose! tiled-map))

(defn render
  [{:keys [ctx/input
           ctx/camera
           ctx/zoom-speed
           ctx/camera-movement-speed
           ctx/sprite-batch
           ctx/tiled-map
           ctx/world-viewport
           ctx/world-unit-scale
           ctx/graphics
           ctx/stage] :as ctx}]
  (let [gl (graphics/get-gl20 graphics)]
    (gl20/gl-clear-color! gl 0 0 0 0)
    (gl20/gl-clear! gl gl20/gl-color-buffer-bit))
  (moon-tiled-map/draw! tiled-map
                        sprite-batch
                        world-unit-scale
                        (viewport/get-camera world-viewport)
                        (constantly (color/to-float-bits [1 1 1 1])))
  (when (input/key-pressed? input :input.keys/minus)
    (orthographic-camera/inc-zoom! camera zoom-speed))
  (when (input/key-pressed? input :input.keys/equals)
    (orthographic-camera/inc-zoom! camera (- zoom-speed)))
  (let [apply-position (fn [idx f]
                         (orthographic-camera/set-position! camera
                                        (update (orthographic-camera/position camera)
                                                idx
                                                #(f % camera-movement-speed))))]
    (when (input/key-pressed? input :input.keys/left)
      (apply-position 0 -))
    (when (input/key-pressed? input :input.keys/right)
      (apply-position 0 +))
    (when (input/key-pressed? input :input.keys/up)
      (apply-position 1 +))
    (when (input/key-pressed? input :input.keys/down)
      (apply-position 1 -)))
  (stage/act! stage)
  (stage/draw! stage)
  ctx)

(defn resize
  [{:keys [ctx/stage
           ctx/world-viewport]}
   width height]
  (viewport/update! (:stage/viewport stage) width height true)
  (viewport/update! world-viewport width height false))

(defn -main []
  (lwjgl-application/use-glfw-async!)
  (lwjgl-application/create
   {:application/listener {:create! (fn [application]
                                      (reset! state (create state config application)))
                           :dispose! (fn []
                                       (dispose @state))
                           :render! (fn []
                                      (swap! state render))
                           :resize! (fn [width height]
                                      (resize @state width height))
                           :pause! (fn [])
                           :resume! (fn [])}
    :application/config {:config/set-title "Levelgen Test"
                         :config/set-windowed-mode {:width 1440
                                                    :height 900}
                         :config/set-foreground-fps 60}}))
