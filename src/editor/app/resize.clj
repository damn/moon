(ns editor.app.resize
  (:require [clojure.gdx.utils.viewport :as viewport]))

(defn resize!
  [{:keys [ctx/stage]} width height]
  (viewport/update! (:stage/viewport stage) width height true))
