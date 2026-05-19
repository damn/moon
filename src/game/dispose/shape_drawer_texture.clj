(ns game.dispose.shape-drawer-texture
  (:require [gdl.utils.disposable :refer [dispose!]]))

(defn do!
  [{:keys [ctx/shape-drawer-texture]}]
  (dispose! shape-drawer-texture))
