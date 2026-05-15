(ns moon.dispose.audio
  (:require [clojure.gdx.utils.disposable :refer [dispose!]]))

(defn do!
  [{:keys [ctx/audio]}]
  (run! dispose! (vals audio)))
