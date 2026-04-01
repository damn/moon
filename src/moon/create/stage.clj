(ns moon.create.stage
  (:require [clj.api.moon.stage :as stage]
            [moon.stage]
            [moon.viewport :as viewport])
  (:import (moon Stage))) ; TODO ??? for extend-type ..., combine w. stuff ? skin ? ui ?

(defn step
  [{:keys [ctx/batch
           ctx/ui-viewport]
    :as ctx}]
  (assoc ctx :ctx/stage (stage/create ui-viewport batch)))

(extend-type Stage
  moon.stage/Stage
  (ctx [stage]
    (.ctx stage))

  (set-ctx! [stage ctx]
    (set! (.ctx stage) ctx))

  (add-actor! [stage actor]
    (.addActor stage actor))

  (find-actor [stage name]
    (-> stage
        .getRoot
        (.findActor name)))

  (mouseover-actor [stage position]
    (let [[x y] (viewport/unproject (.getViewport stage) position)]
      (.hit stage x y true)))

  (viewport [stage]
    (.getViewport stage))

  (act! [stage]
    (.act stage))

  (draw! [stage]
    (.draw stage)))
