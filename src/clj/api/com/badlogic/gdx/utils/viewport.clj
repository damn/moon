(ns clj.api.com.badlogic.gdx.utils.viewport
  (:import (com.badlogic.gdx.utils.viewport Viewport)))

(defn camera [^Viewport viewport]
  (.getCamera viewport))
