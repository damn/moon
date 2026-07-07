(ns clojure.ctx-button-just-pressed
  (:require [clojure.input-button-just-pressed :as button-just-pressed?]))

(defn button-just-pressed?
  [{:keys [ctx/input]} button-code]
  (button-just-pressed?/f input button-code))
