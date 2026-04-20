(ns moon.impl.stage
  (:require [clojure.gdx.scene2d.stage :as stage]))

(defn create
  [{:keys [ctx/batch
           ctx/ui-viewport]}]
  (stage/create ui-viewport batch))
