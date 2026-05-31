(ns render.validate
  (:require [malli.utils :as mu]))

(defn step [ctx]
  (mu/validate-humanize (:ctx/schema ctx) ctx)
  ctx)
