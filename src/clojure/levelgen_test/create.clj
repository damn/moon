(ns clojure.levelgen-test.create
  (:require 
            [clojure.table-set-opts :as table-set-opts]
            [com.badlogic.gdx.application :as application]
            [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [clojure.db.all-raw :refer [all-raw]]
            [clojure.files.create-textures :as create-textures]
            [com.badlogic.gdx.utils.disposable :as disposable]
            [com.badlogic.gdx.files :as files]
            [com.badlogic.gdx.utils.viewport.fit-viewport :as fit-viewport]
            [com.badlogic.gdx.maps.map-layers :as map-layers]
            [clojure.tiled-map.get-property :as get-property]
            [com.badlogic.gdx.input :as input]
            [clojure.malli-form-register-methods]
            [clojure.moon-db :as db]
            [clojure.moon-textures :as textures]
            [com.badlogic.gdx.graphics.orthographic-camera :as orthographic-camera]
            [clojure.orthographic-camera-set-position :refer [set-position!]]
            [clojure.scene2d-stage]
            [com.badlogic.gdx.scenes.scene2d.ui.skin :as skin]
            [com.badlogic.gdx.graphics.g2d.sprite-batch :as sprite-batch]
            [com.badlogic.gdx.scenes.scene2d.stage :as stage]
            [clojure.tiled-map.creature-tiles :as creature-tiles]
            [com.badlogic.gdx.maps.tiled.tiled-map :as tiled-map]
            [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer :as tiled-map-tile-layer]
            [com.badlogic.gdx.scenes.scene2d.ui.text-button :as text-button]
            [com.badlogic.gdx.scenes.scene2d.ui.window :as window]
            [com.badlogic.gdx.utils.viewport.viewport :as viewport]
            [com.badlogic.gdx.scenes.scene2d.utils.change-listener :as change-listener]
            [clojure.orthographic-camera.zoom-to-rect :as zoom-to-rect]))

(defn- generate-level
  [{:keys [ctx/camera
           ctx/db
           ctx/textures]
    :as ctx}
   level-fn]
  (let [level (level-fn
               {:level/creature-properties (creature-tiles/prepare
                                            (all-raw db :properties/creatures)
                                            #(textures/texture-region textures %))
                :textures textures})
        tiled-map (:tiled-map level)
        ctx (assoc ctx :ctx/tiled-map tiled-map)]
    (assert tiled-map)
    (-> tiled-map
        tiled-map/getLayers
        (map-layers/get "creatures")
        (tiled-map-tile-layer/setVisible true))
    (set-position! camera [(/ (get-property/f tiled-map "width") 2)
                           (/ (get-property/f tiled-map "height") 2)])
    (zoom-to-rect/f camera {:left [0 0]
                            :top [0 (get-property/f tiled-map "height")]
                            :right [(get-property/f tiled-map "width") 0]
                            :bottom [0 0]})
    ctx))

(defn create
  [state config application]
  (let [files (application/getFiles application)
        input (application/getInput application)
        graphics (application/getGraphics application)
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
                                                (orthographic-camera/setToOrtho false world-width world-height))))
        ctx {:ctx/input input
             :ctx/graphics graphics
             :ctx/stage stage
             :ctx/zoom-speed (:zoom-speed config)
             :ctx/camera-movement-speed (:camera-movement-speed config)
             :ctx/world-unit-scale world-unit-scale
             :ctx/db (db/create)
             :ctx/textures (create-textures/f files (:textures-config config))
             :ctx/sprite-batch sprite-batch
             :ctx/skin skin
             :ctx/world-viewport world-viewport
             :ctx/camera (viewport/getCamera world-viewport)}
        ctx (generate-level ctx (:initial-level-fn config))]
    (input/setInputProcessor input stage)
    (stage/addActor (:ctx/stage ctx)
                      (doto (window/new "Edit" skin)
    (table-set-opts/set-opts! {:title "Edit"
                        :skin skin
                        :table/rows (for [[label level-fn] (:level-fns config)
                                          :let [on-click #(do
                                                            (disposable/dispose (:ctx/tiled-map %))
                                                            (generate-level % level-fn))]]
                                      [{:actor (doto (text-button/new (str "Generate " label) skin)
                                               (actor/addListener
                                                (change-listener/create
                                                 (fn [_event _actor]
                                                   (swap! state on-click)))))}])})))
    ctx))
