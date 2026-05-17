(ns game.dispose.default-font
  (:require [clojure.gdx.utils.disposable :refer [dispose!]]))

(defn do!
  [{:keys [ctx/default-font]}]
  (dispose! default-font))
