(ns clojure.levelgen-test.create
  (:require [clojure.actor.add-listener]
            [clojure.db.all-raw :refer [all-raw]]
            [clojure.create-textures :as create-textures]
            [clojure.creature-tiles :as creature-tiles]
            [clojure.disposable :as disposable]
            [clojure.files :as files]
            [clojure.fit-viewport :as fit-viewport]
            [clojure.map-layers.get :as get]
            [clojure.get-property :as get-property]
            [clojure.input :as input]
            [clojure.malli-form-register-methods]
            [clojure.moon-db :as db]
            [clojure.moon-textures :as textures]
            [clojure.orthographic-camera :as orthographic-camera]
            [clojure.orthographic-camera-set-position :refer [set-position!]]
            [clojure.scene2d-stage]
            [clojure.skin :as skin]
            [clojure.sprite-batch :as sprite-batch]
            [clojure.stage :as stage]
            [clojure.tiled-map :as tiled-map]
            [clojure.tiled-map-tile-layer :as tiled-map-tile-layer]
            [clojure.ui-text-button :as text-button]
            [clojure.ui-window :as window]
            [clojure.utils-change-listener :as utils-change-listener]
            [clojure.zoom-to-rect :as zoom-to-rect]))

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
        tiled-map/get-layers
        (get/f "creatures")
        (tiled-map-tile-layer/set-visible! true))
    (set-position! camera [(/ (get-property/f tiled-map "width") 2)
                           (/ (get-property/f tiled-map "height") 2)])
    (zoom-to-rect/f camera {:left [0 0]
                            :top [0 (get-property/f tiled-map "height")]
                            :right [(get-property/f tiled-map "width") 0]
                            :bottom [0 0]})
    ctx))

(defn create
  [state config ^com.badlogic.gdx.Application app]
  (let [files (.getFiles app)
        input (.getInput app)
        graphics (.getGraphics app)
        sprite-batch (sprite-batch/new)
        ui-viewport (fit-viewport/create (:ui-viewport-width config)
                                         (:ui-viewport-height config))
        world-unit-scale (float (/ (:tile-size config)))
        stage (clojure.scene2d-stage/create ui-viewport sprite-batch)
        skin (skin/new (files/internal files (:ui-skin-path config)))
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
             :ctx/textures (create-textures/f files (:textures-config config))
             :ctx/sprite-batch sprite-batch
             :ctx/skin skin
             :ctx/world-viewport world-viewport
             :ctx/camera (:viewport/camera world-viewport)}
        ctx (generate-level ctx (:initial-level-fn config))]
    (input/set-input-processor! input stage)
    (stage/add-actor! (:ctx/stage ctx)
                      (window/create
                       {:title "Edit"
                        :skin skin
                        :table/rows (for [[label level-fn] (:level-fns config)
                                          :let [on-click #(do
                                                            (disposable/dispose! (:ctx/tiled-map %))
                                                            (generate-level % level-fn))]]
                                      [{:actor (doto (text-button/create
                                                      {:text (str "Generate " label)
                                                       :skin skin})
                                               (clojure.actor.add-listener/f
                                                (utils-change-listener/create
                                                 (fn [_event _actor]
                                                   (swap! state on-click)))))}])}))
    ctx))
