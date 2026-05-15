(ns clojure.animation)

(defn tick
  [{:keys [cnt
           maxcnt
           looping?]
    :as this}
   delta]
  (let [maxcnt (float maxcnt)
        newcnt (+ (float cnt) (float delta))]
    (assoc this :cnt (cond (< newcnt maxcnt) newcnt
                           looping? (min maxcnt (- newcnt maxcnt))
                           :else maxcnt))))
(defn stopped?
  [{:keys [looping?
           cnt
           maxcnt]}]
  (and (not looping?) (>= cnt maxcnt)))

(defn current-frame
  [{:keys [frames
           cnt
           frame-duration]}]
  (frames (min (int (/ (float cnt) (float frame-duration)))
               (dec (count frames)))))
