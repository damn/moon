(ns levelgen-test.create
  (:require [com.badlogic.gdx.files :as files]
            [com.badlogic.gdx.graphics.color.float-bits :refer [float-bits]]
            [com.badlogic.gdx.graphics.g2d.sprite-batch :as sprite-batch]
            [com.badlogic.gdx.input.set-processor :as set-processor!]
            [com.badlogic.gdx.scenes.scene2d.ui.skin :as skin]
            [com.badlogic.gdx.utils.fit-viewport :as fit-viewport]
            [com.badlogic.gdx.graphics.orthographic-camera :as camera]
            [gdx.scenes.scene2d.ui.window :as window]
            [com.badlogic.gdx.scenes.scene2d.stage :as stage]
            [com.badlogic.gdx.scenes.scene2d.stage.add-actor :refer [add-actor!]]
            [gdx.textures]
            [levelgen-test.create.edit-window :refer [edit-window]]
            [levelgen-test.generate-level :as generate-level]
            [moon.db :as db]))

(def initial-level-fn "config/world_fns/uf_caves.edn")

(def level-fns
  ["config/world_fns/vampire.edn"
   "config/world_fns/uf_caves.edn"
   "config/world_fns/modules.edn"])

(defn f!
  [{:keys [ctx/files
           ctx/graphics
           ctx/input]}]
  (let [skin (skin/create (files/internal files "skin/uiskin.json"))
        ui-viewport (fit-viewport/create 1440 900)
        sprite-batch (sprite-batch/create)
        stage (stage/create ui-viewport sprite-batch)
        _  (set-processor!/f input stage)
        tile-size 48
        world-unit-scale (float (/ tile-size))
        ctx {:ctx/stage stage
             :ctx/files files}
        ctx (assoc ctx :ctx/db (db/create))
        ctx (assoc ctx :ctx/textures (gdx.textures/create files))
        world-viewport (let [world-width  (* 1440 world-unit-scale)
                             world-height (* 900  world-unit-scale)]
                         (fit-viewport/create world-width
                                              world-height
                                              (camera/create
                                               {:y-down? false
                                                :world-width world-width
                                                :world-height world-height})))
        ctx (assoc ctx
                   :ctx/input input
                   :ctx/world-viewport world-viewport
                   :ctx/camera (:viewport/camera world-viewport)
                   :ctx/color-setter (constantly (float-bits [1 1 1 1]))
                   :ctx/zoom-speed 0.1
                   :ctx/camera-movement-speed 1
                   :ctx/sprite-batch sprite-batch
                   :ctx/skin skin
                   :ctx/world-unit-scale world-unit-scale)
        ctx (generate-level/f ctx initial-level-fn)]
    (add-actor! (:ctx/stage ctx) (window/create (edit-window skin level-fns)))
    ctx))
