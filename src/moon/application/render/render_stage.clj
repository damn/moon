(ns moon.application.render.render-stage
  (:import (gdl.ui Stage)))

(defn step
  [{:keys [^Stage ctx/stage]
    :as ctx}]
  (set! (.ctx stage) ctx)
  (.act  stage)
  (.draw stage)
  (.ctx  stage))
