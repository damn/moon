(ns animation.create)

(defn create
  [{:keys [animation/frames
           animation/frame-duration
           animation/looping?
           delete-after-stopped?]}]
  (assert (not (and looping? delete-after-stopped?)))
  {:frames (vec frames)
   :frame-duration frame-duration
   :looping? looping?
   :cnt 0
   :maxcnt (* (count frames) (float frame-duration))
   :delete-after-stopped? delete-after-stopped?})
