(ns clojure.editor.skin
  (:require [clojure.ctx-skin :as ctx-skin]))

(defn f [ctx]
  (assoc ctx :ctx/skin (ctx-skin/step ctx)))
