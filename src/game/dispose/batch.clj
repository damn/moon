(ns game.dispose.batch
  (:require [clojure.gdx.utils.disposable :refer [dispose!]]))

(defn do!
  [{:keys [ctx/batch]}]
  (dispose! batch))
