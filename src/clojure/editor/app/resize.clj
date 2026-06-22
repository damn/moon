(ns clojure.editor.app.resize
  (:require [gdl.viewport.update :as update!]))

(defn resize!
  [{:keys [ctx/stage]} width height]
  (update!/f (:stage/viewport stage) width height true))
