(ns moon.render.render-stage ; TODO makes 2 things - draw/act - separate further
  (:import (moon Stage)))

(defn do!
  [{:keys [^Stage ctx/stage]
    :as ctx}]
  (set! (.ctx stage) ctx)
  (.act  stage)
  (.draw stage)
  (.ctx  stage))
