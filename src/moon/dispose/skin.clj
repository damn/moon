(ns moon.dispose.skin
  (:import (com.badlogic.gdx.utils Disposable)))

(defn do!
  [{:keys [ctx/skin]}]
  (Disposable/.dispose skin))
