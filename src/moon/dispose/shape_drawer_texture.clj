(ns moon.dispose.shape-drawer-texture
  (:import (com.badlogic.gdx.utils Disposable)))

(defn do!
  [{:keys [ctx/shape-drawer-texture]}]
  (Disposable/.dispose shape-drawer-texture))
