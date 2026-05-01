(ns moon.application.create.stage
  (:import (clojure.gdx Stage)
           (com.badlogic.gdx.graphics OrthographicCamera)
           (com.badlogic.gdx.utils.viewport FitViewport)))

(defn step
  [{:keys [ctx/app
           ctx/batch]
    :as ctx}]
  (assoc ctx :ctx/stage (let [stage (Stage. (FitViewport. 1440 900 (OrthographicCamera.))
                                            batch)]
                          (.setInputProcessor (.getInput app) stage)
                          stage)))
