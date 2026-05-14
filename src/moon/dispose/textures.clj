(ns moon.dispose.textures
  (:import (com.badlogic.gdx.utils Disposable)))

(defn do!
  [{:keys [ctx/textures]}]
  (run! Disposable/.dispose (vals textures)))
