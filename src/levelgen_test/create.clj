(ns levelgen-test.create
  (:require [clojure.files :as files]
            [clojure.rgba.float-bits :refer [float-bits]]
            [clojure.input.set-processor :as set-processor!]
            [clojure.utils.fit-viewport :as fit-viewport]
            [clojure.orthographic-camera :as camera]
            [gdx.scenes.scene2d.ui.window :as window]
            [clojure.scenes.scene2d.stage :as stage]
            [clojure.scenes.scene2d.stage.add-actor :refer [add-actor!]]
            [levelgen-test.create.edit-window :refer [edit-window]]
            [levelgen-test.generate-level :as generate-level]))

(def initial-level-fn "config/world_fns/uf_caves.edn")

(def level-fns
  ["config/world_fns/vampire.edn"
   "config/world_fns/uf_caves.edn"
   "config/world_fns/modules.edn"])

(defn f!
  [{:keys [ctx/files
           ctx/input
           ctx/sprite-batch]
    :as ctx}]
  (let [ui-viewport (fit-viewport/create 1440 900)
        stage (stage/create ui-viewport sprite-batch)
        _  (set-processor!/f input stage)
        tile-size 48
        world-unit-scale (float (/ tile-size))
        ctx (assoc ctx :ctx/stage stage)
        world-viewport (let [world-width  (* 1440 world-unit-scale)
                             world-height (* 900  world-unit-scale)]
                         (fit-viewport/create world-width
                                              world-height
                                              (camera/create
                                               {:y-down? false
                                                :world-width world-width
                                                :world-height world-height})))
        ctx (assoc ctx
                   :ctx/world-viewport world-viewport
                   :ctx/camera (:viewport/camera world-viewport)
                   :ctx/color-setter (constantly (float-bits [1 1 1 1]))
                   :ctx/zoom-speed 0.1
                   :ctx/camera-movement-speed 1
                   :ctx/world-unit-scale world-unit-scale)
        ctx (generate-level/f ctx initial-level-fn)]
    (add-actor! (:ctx/stage ctx) (window/create (edit-window (:ctx/skin ctx) level-fns)))
    ctx))
