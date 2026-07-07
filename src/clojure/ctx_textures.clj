(ns clojure.ctx-textures
  (:require [clojure.create-textures :as create-textures]))

(defn step
  [{:keys [ctx/files]} config]
  (create-textures/f files config))
