(ns clojure.editor.create.ctx-skin-step
  (:require [clojure.ctx-skin :as ctx-skin]))

(defn f [ctx]
  (assoc ctx :ctx/skin (ctx-skin/step ctx)))
