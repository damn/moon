(ns clojure.editor.ctx-db-step
  (:require [clojure.ctx-db :as ctx-db]))

(defn f [ctx]
  (assoc ctx :ctx/db (ctx-db/step ctx)))
