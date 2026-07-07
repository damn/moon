(ns clojure.levelgen-test-create
  (:require
            [clojure.add-listener] [clojure.orthographic-camera :as orthographic-camera]
            [clojure.stage :as stage]
            [clojure.input :as input]
            [clojure.disposable :as disposable]
            [clojure.files :as files]
            [clojure.fit-viewport :as fit-viewport]
            [clojure.gdx :as gdx]
            [clojure.skin :as skin]
            [clojure.sprite-batch :as sprite-batch]
            [clojure.ui-window :as window]
            [clojure.levelgen-test-change-listener]
            [clojure.levelgen-test-generate :as generate]
            [clojure.create-textures :as create-textures]
            [clojure.moon-db :as db]
            [clojure.scene2d-stage]
            [clojure.ui-text-button :as text-button]))

(defn f
  [config]
  (let [files (gdx/files)
        input (gdx/input)
        graphics (gdx/graphics)
        sprite-batch (sprite-batch/new)
        ui-viewport (fit-viewport/create (:ui-viewport-width config)
                                         (:ui-viewport-height config))
        world-unit-scale (float (/ (:tile-size config)))
        stage (clojure.scene2d-stage/create ui-viewport sprite-batch)
        skin (skin/new (files/internal files (:ui-skin-path config)))
        world-viewport (let [world-width  (* (:world-viewport-width config) world-unit-scale)
                             world-height (* (:world-viewport-height config)  world-unit-scale)]
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
        ctx (generate/f ctx (:initial-level-fn config))]
    (input/set-input-processor! input stage)
    (stage/add-actor! (:ctx/stage ctx)
                 (window/create
                  {:title "Edit"
                   :skin skin
                   :table/rows (for [level-fn (:level-fns config)
                                     :let [on-click #(do
                                                      (disposable/dispose! (:ctx/tiled-map %))
                                                      (generate/f % level-fn))]]
                                 [{:actor (doto (text-button/create
                                                 {:text (str "Generate " level-fn)
                                                  :skin skin})
                                            (clojure.add-listener/f (clojure.levelgen-test-change-listener/f
                                                             on-click)))}])}))
    ctx))
