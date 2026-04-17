(ns moon.create.tooltip-config
  (:require [clojure.gdx.tooltip-manager :as tooltip-manager]))

(defn step [ctx params]
  (tooltip-manager/configure! params)
  ctx)
