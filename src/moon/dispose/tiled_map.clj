(ns moon.dispose.tiled-map
  (:import (com.badlogic.gdx.utils Disposable)))

(defn do!
  [{:keys [ctx/tiled-map]}]
  (Disposable/.dispose tiled-map))
