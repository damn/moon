(ns clojure.moon.create-stage
  (:require [clojure.ctx-stage :as ctx-stage]))

(defn f [ctx]
  (assoc ctx :ctx/stage (ctx-stage/step ctx)))
