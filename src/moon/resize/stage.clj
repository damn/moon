(ns moon.resize.stage
  (:require [moon.stage :as stage]))

(defn do!
  [{:keys [ctx/stage]} width height]
  (stage/update-viewport! stage width height))
