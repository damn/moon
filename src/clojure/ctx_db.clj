(ns clojure.ctx-db
  (:require [clojure.moon-db :as db]))

(defn step [_ctx]
  (db/create))
