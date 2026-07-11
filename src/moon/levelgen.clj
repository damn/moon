(ns moon.levelgen
  (:require [moon.db :as db]
            [moon.orthographic-camera :as orthographic-camera]
            [moon.level.modules :as modules]
            [moon.level.tmx :as tmx]
            [moon.level.uf-caves :as uf-caves]
            [clojure.malli-form-register-methods]
            [moon.textures :as textures]
            [clojure.scene2d-stage]
            [clojure.table-set-opts :as table-set-opts]
            [clojure.tiled-map.creature-tiles :as creature-tiles]
            [clojure.tiled-map.get-property :as get-property]
            [moon.files :as files]
            [com.badlogic.gdx.graphics :as graphics]
            [com.badlogic.gdx.graphics.color :as color]
            [com.badlogic.gdx.graphics.g2d.sprite-batch :as sprite-batch]
            [com.badlogic.gdx.graphics.gl20 :as gl20]
            [moon.input :as input]
            [com.badlogic.gdx.maps.map-layers :as map-layers]
            [com.badlogic.gdx.maps.tiled.tiled-map :as tiled-map]
            [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer :as tiled-map-tile-layer]
            [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [com.badlogic.gdx.scenes.scene2d.stage :as stage]
            [com.badlogic.gdx.scenes.scene2d.ui.skin :as skin]
            [com.badlogic.gdx.scenes.scene2d.ui.text-button :as text-button]
            [com.badlogic.gdx.scenes.scene2d.ui.window :as window]
            [com.badlogic.gdx.scenes.scene2d.utils.change-listener :as change-listener]
            [moon.disposable :as disposable]
            [com.badlogic.gdx.utils.viewport.fit-viewport :as fit-viewport]
            [com.badlogic.gdx.utils.viewport.viewport :as viewport]
            [moon.application :as application]
            [gdx.graphics.g2d.batch.draw-tiled-map :as draw-tiled-map]))

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
               {:level/creature-properties (creature-tiles/prepare
                                            (db/all-raw db :properties/creatures)
                                            #(textures/texture-region textures %))
                :textures textures})
        tiled-map (:tiled-map level)
        ctx (assoc ctx :ctx/tiled-map tiled-map)]
    (assert tiled-map)
    (-> tiled-map
        tiled-map/getLayers
        (map-layers/get "creatures")
        (tiled-map-tile-layer/setVisible true))
    (orthographic-camera/set-position! camera [(/ (get-property/f tiled-map "width") 2)
                           (/ (get-property/f tiled-map "height") 2)])
    (orthographic-camera/zoom-to-rect camera {:left [0 0]
                            :top [0 (get-property/f tiled-map "height")]
                            :right [(get-property/f tiled-map "width") 0]
                            :bottom [0 0]})
    ctx))

(defn create
  [state config application]
  (let [files (application/get-files application)
        input (application/get-input application)
        graphics (application/get-graphics application)
        sprite-batch (sprite-batch/new)
        ui-viewport (fit-viewport/new (:ui-viewport-width config)
                                      (:ui-viewport-height config))
        world-unit-scale (float (/ (:tile-size config)))
        stage (clojure.scene2d-stage/create ui-viewport sprite-batch)
        skin (skin/new (files/internal files (:ui-skin-path config)))
        world-viewport (let [world-width (* (:world-viewport-width config) world-unit-scale)
                             world-height (* (:world-viewport-height config) world-unit-scale)]
                         (fit-viewport/new world-width
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
             :ctx/camera (viewport/getCamera world-viewport)}
        ctx (generate-level ctx (:initial-level-fn config))]
    (input/set-processor! input stage)
    (stage/addActor (:ctx/stage ctx)
                    (doto (window/new "Edit" skin)
                      (table-set-opts/set-opts! {:title "Edit"
                                                 :skin skin
                                                 :table/rows (for [[label level-fn] (:level-fns config)
                                                                   :let [on-click #(do
                                                                                     (disposable/dispose! (:ctx/tiled-map %))
                                                                                     (generate-level % level-fn))]]
                                                               [{:actor (doto (text-button/new (str "Generate " label) skin)
                                                                              (actor/addListener
                                                                               (change-listener/create
                                                                                (fn [_event _actor]
                                                                                  (swap! state on-click)))))}])})))
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
  (let [gl (graphics/getGL20 graphics)]
    (gl20/glClearColor gl 0 0 0 0)
    (gl20/glClear gl gl20/GL_COLOR_BUFFER_BIT))
  (draw-tiled-map/draw! sprite-batch
                        world-unit-scale
                        (viewport/getCamera world-viewport)
                        tiled-map
                        (constantly (color/toFloatBits [1 1 1 1])))
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
  (stage/act stage)
  (stage/draw stage)
  ctx)

(defn resize
  [{:keys [ctx/stage
           ctx/world-viewport]}
   width height]
  (viewport/update (:stage/viewport stage) width height true)
  (viewport/update world-viewport width height false))

(defn -main []
  (application/create
   {:create! (fn [app]
               (reset! state (create state config app)))
    :dispose! (fn []
                (dispose @state))
    :render! (fn []
               (swap! state render))
    :resize! (fn [width height]
               (resize @state width height))
    :pause! (fn [])
    :resume! (fn [])}
   {:config/set-title "Levelgen Test"
    :config/set-windowed-mode {:width 1440
                               :height 900}
    :config/set-foreground-fps 60}))
