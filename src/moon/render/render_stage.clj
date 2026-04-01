(ns moon.render.render-stage ; TODO makes 2 things - draw/act - separate further
  (:require [moon.stage :as stage]))

(defn do!
  [{:keys [ctx/stage] :as ctx}]
  (stage/set-ctx! stage ctx)
  (stage/act!  stage)
  (stage/draw! stage)
  (stage/ctx stage))
