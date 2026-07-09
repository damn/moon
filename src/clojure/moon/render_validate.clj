(ns clojure.moon.render-validate
  (:require [clojure.moon.schema :refer [schema]]
            [clojure.malli.schema :as malli-schema]))

(defn f [ctx]
  (malli-schema/validate-humanize schema ctx)
  ctx)
