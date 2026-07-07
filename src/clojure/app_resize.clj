(ns clojure.app-resize
  (:require [clojure.viewport :as viewport]))

(defn resize!
  [{:keys [ctx/stage]} width height]
  (viewport/update! (:stage/viewport stage) width height true))
