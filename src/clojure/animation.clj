(ns clojure.animation)

(defprotocol Animation
  (tick [_ delta])
  (stopped? [_])
  (current-frame [_]))

(defrecord RAnimation
  [frames frame-duration looping? cnt maxcnt delete-after-stopped?]
  Animation
  (tick [this delta]
    (let [maxcnt (float maxcnt)
          newcnt (+ (float cnt) (float delta))]
      (assoc this :cnt (cond (< newcnt maxcnt) newcnt
                             looping? (min maxcnt (- newcnt maxcnt))
                             :else maxcnt))))

  (stopped? [_]
    (and (not looping?) (>= cnt maxcnt)))

  (current-frame [this]
    (frames (min (int (/ (float cnt) (float frame-duration)))
                 (dec (count frames))))))
