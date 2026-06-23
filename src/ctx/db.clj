(ns ctx.db
  (:require [moon.db :as db]))

(defn step [_ctx]
  (db/create))
