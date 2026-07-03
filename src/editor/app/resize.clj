(ns editor.app.resize
  (:require [clojure.gdx.viewport.update :as update-viewport]))

(defn resize!
  [{:keys [ctx/stage]} width height]
  (update-viewport/f (:stage/viewport stage) width height true))
