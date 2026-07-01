(ns levelgen-test.create
  (:require [clojure.gdx.float-bits :as float-bits]
            [gdx.scenes.scene2d.ui.window :as window]
            [scene2d.stage :as stage]
            [levelgen-test.create.edit-window :refer [edit-window]]
            [levelgen-test.generate-level :as generate-level]
            [clojure.gdx.fit-viewport :as fit-viewport])
  (:import (com.badlogic.gdx Input)
           (com.badlogic.gdx.graphics OrthographicCamera)
           (com.badlogic.gdx.scenes.scene2d Stage)))

(defn f!
  [{:keys [ctx/files
           ctx/input
           ctx/sprite-batch]
    :as ctx}]
  (let [initial-level-fn "config/world_fns/uf_caves.edn"
        level-fns ["config/world_fns/vampire.edn"
                   "config/world_fns/uf_caves.edn"
                   "config/world_fns/modules.edn"]
        ui-viewport (fit-viewport/create 1440 900)
        stage (stage/create ui-viewport sprite-batch)
        _  (.setInputProcessor ^Input input stage)
        tile-size 48
        world-unit-scale (float (/ tile-size))
        ctx (assoc ctx :ctx/stage stage)
        world-viewport (let [world-width  (* 1440 world-unit-scale)
                             world-height (* 900  world-unit-scale)]
                         (fit-viewport/create world-width
                                              world-height
                                              (doto (OrthographicCamera.)
                                                (.setToOrtho false world-width world-height))))
        ctx (assoc ctx
                   :ctx/world-viewport world-viewport
                   :ctx/camera (:viewport/camera world-viewport)
                   :ctx/color-setter (constantly (float-bits/f [1 1 1 1]))
                   :ctx/zoom-speed 0.1
                   :ctx/camera-movement-speed 1
                   :ctx/world-unit-scale world-unit-scale)
        ctx (generate-level/f ctx initial-level-fn)]
    (Stage/.addActor (:ctx/stage ctx) (window/create (edit-window (:ctx/skin ctx) level-fns)))
    ctx))
