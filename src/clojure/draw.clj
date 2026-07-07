(ns clojure.draw)

(defn draw! [{:keys [ctx/draw-fns] :as ctx} draws]
  (doseq [{k 0 :as component} draws
          :when component]
    (apply (get draw-fns k) ctx (rest component))))
