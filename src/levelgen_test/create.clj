(ns levelgen-test.create
  (:require [clojure.gdx.actor.add-listener :as add-listener]
            [com.badlogic.gdx.utils.disposable :as disposable]
            [com.badlogic.gdx.files :as files]
            [com.badlogic.gdx.utils.viewport.fit-viewport :as fit-viewport]
            [com.badlogic.gdx.gdx :as gdx]
            [clojure.gdx.input.set-input-processor! :as set-input-processor!]
            [clojure.gdx.orthographic-camera.new :as new-camera]
            [clojure.gdx.orthographic-camera.set-to-ortho :as set-to-ortho!]
            [com.badlogic.gdx.scenes.scene2d.ui.skin :as skin]
            [com.badlogic.gdx.graphics.g2d.sprite-batch :as sprite-batch]
            [clojure.gdx.stage.add-actor :as add-actor]
            [gdx.scenes.scene2d.ui.window :as window]
            [levelgen-test.change-listener]
            [levelgen-test.generate :as generate]
            [moon.create-textures :as create-textures]
            [moon.db :as db]
            [scene2d.stage :as stage]
            [scene2d.ui.text-button :as text-button]))

(defn f
  [config]
  (let [files (gdx/files)
        input (gdx/input)
        graphics (gdx/graphics)
        sprite-batch (sprite-batch/new)
        ui-viewport (fit-viewport/create (:ui-viewport-width config)
                                         (:ui-viewport-height config))
        world-unit-scale (float (/ (:tile-size config)))
        stage (stage/create ui-viewport sprite-batch)
        skin (skin/new (files/internal files (:ui-skin-path config)))
        world-viewport (let [world-width  (* (:world-viewport-width config) world-unit-scale)
                             world-height (* (:world-viewport-height config)  world-unit-scale)]
                         (fit-viewport/create world-width
                                              world-height
                                              (doto (new-camera/f)
                                                (set-to-ortho!/f! false world-width world-height))))
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
    (set-input-processor!/f input stage)
    (add-actor/f (:ctx/stage ctx)
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
                                            (add-listener/f (levelgen-test.change-listener/f
                                                             on-click)))}])}))
    ctx))
