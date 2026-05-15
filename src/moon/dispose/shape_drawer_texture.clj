(ns moon.dispose.shape-drawer-texture
  (:require [clojure.gdx.utils.disposable :refer [dispose!]]))

(defn do!
  [{:keys [ctx/shape-drawer-texture]}]
  (dispose! shape-drawer-texture))
