(ns render.update-draw-stage
  (:import (moon Stage)))

(defn step
  [{:keys [ctx/stage] :as ctx}]
  (set! (.ctx stage) ctx)
  (Stage/.act stage)
  (Stage/.draw stage)
  (:stage/ctx stage))
