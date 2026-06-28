(ns editor.app.resize
  (:import (com.badlogic.gdx.utils.viewport Viewport)))

(defn resize!
  [{:keys [ctx/stage]} width height]
  (.update ^Viewport (:stage/viewport stage) width height true))
