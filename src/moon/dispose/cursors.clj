(ns moon.dispose.cursors
  (:import (com.badlogic.gdx.utils Disposable)))

(defn do!
  [{:keys [ctx/cursors]}]
  (run! Disposable/.dispose (vals cursors)))
