(ns clojure.moon.create-dissoc-files)

(defn f [ctx]
  (dissoc ctx :ctx/files))
