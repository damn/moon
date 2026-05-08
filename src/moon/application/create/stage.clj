(ns moon.application.create.stage
  (:require [clojure.gdx.utils.viewport :as viewport]
            [com.badlogic.gdx.application :as app]
            [com.badlogic.gdx.graphics.orthographic-camera :as orthographic-camera]
            [com.badlogic.gdx.input :as input]
            [com.badlogic.gdx.scenes.scene2d.ctx-stage :as stage]
            [com.badlogic.gdx.utils.viewport.fit-viewport :as fit-viewport]
            [moon.stage]
            [moon.ui.group :as group]))

(defn step
  [{:keys [ctx/app
           ctx/batch]
    :as ctx}]
  (assoc ctx :ctx/stage (let [stage (stage/create (fit-viewport/create 1440 900 (orthographic-camera/create))
                                                  batch)]
                          (input/set-processor! (app/input app) stage)
                          stage)))

(extend com.badlogic.gdx.scenes.scene2d.CtxStage
  moon.stage/Stage
  {
   :ctx stage/ctx
   :set-ctx! stage/set-ctx!
   :add-actor! stage/add-actor!
   :viewport stage/viewport
   :act! stage/act!
   :draw! stage/draw!
   :find-actor (fn [stage name]
                 (-> stage
                     stage/root
                     (group/find-actor name)))
   :mouseover-actor (fn [stage position]
                      (stage/hit stage
                                 (viewport/unproject (stage/viewport stage) position)
                                 true))
   })
