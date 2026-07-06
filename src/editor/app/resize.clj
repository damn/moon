(ns editor.app.resize
  (:require [com.badlogic.gdx.utils.viewport.viewport :as viewport]))

(defn resize!
  [{:keys [ctx/stage]} width height]
  (viewport/update! (:stage/viewport stage) width height true))
