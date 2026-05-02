(ns moon.application.create.stage
  (:require [com.badlogic.gdx.utils.viewport.fit-viewport :as fit-viewport])
  (:import (clojure.gdx Stage)
           (com.badlogic.gdx.graphics OrthographicCamera)))

(defn step
  [{:keys [ctx/app
           ctx/batch]
    :as ctx}]
  (assoc ctx :ctx/stage (let [stage (Stage. (fit-viewport/create 1440 900 (OrthographicCamera.))
                                            batch)]
                          (.setInputProcessor (.getInput app) stage)
                          stage)))
