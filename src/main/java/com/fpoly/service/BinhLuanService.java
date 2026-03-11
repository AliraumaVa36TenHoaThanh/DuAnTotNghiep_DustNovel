package com.fpoly.service;

import com.fpoly.model.BinhLuan;
import com.fpoly.model.Chuong;
import com.fpoly.model.NguoiDung;
import com.fpoly.model.Truyen;
import com.fpoly.repository.BinhLuanRepository;
import com.fpoly.repository.ChuongRepository;
import com.fpoly.repository.NguoiDungRepository;
import com.fpoly.repository.TruyenRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BinhLuanService {

    private final BinhLuanRepository binhLuanRepo;
    private final TruyenRepository truyenRepo;
    private final ChuongRepository chuongRepo;
    private final NguoiDungRepository nguoiDungRepo;

    public BinhLuanService(BinhLuanRepository binhLuanRepo,
                           TruyenRepository truyenRepo,
                           ChuongRepository chuongRepo,
                           NguoiDungRepository nguoiDungRepo) {
        this.binhLuanRepo = binhLuanRepo;
        this.truyenRepo = truyenRepo;
        this.chuongRepo = chuongRepo;
        this.nguoiDungRepo = nguoiDungRepo;
    }

    public List<BinhLuan> getByTruyen(Long truyenId) {
        return binhLuanRepo.findByTruyenIdOrderByNgayBinhLuanDesc(truyenId);
    }

    public List<BinhLuan> getByChuong(Long chuongId) {
        return binhLuanRepo.findParentCommentsWithChildren(chuongId);
    }
    public void save(Long truyenId, Long userId, String noiDung) {

        Truyen truyen = truyenRepo.findById(truyenId).orElseThrow(() -> new RuntimeException("Không tìm thấy truyện"));

        NguoiDung user = nguoiDungRepo.findById(userId).orElseThrow(() -> new RuntimeException("Không tìm thấy user"));

        BinhLuan bl = new BinhLuan();
        bl.setTruyen(truyen);
        bl.setNguoiDung(user);
        bl.setNoiDung(noiDung);

        binhLuanRepo.save(bl);
    }
    
    public void saveForChuong(Long chuongId,
            Long userId,
            String noiDung) {

Chuong chuong = chuongRepo.findById(chuongId).orElseThrow(() -> new RuntimeException("Không tìm thấy chương"));

NguoiDung user = nguoiDungRepo.findById(userId).orElseThrow(() -> new RuntimeException("Không tìm thấy user"));

BinhLuan bl = new BinhLuan();
bl.setNoiDung(noiDung);
bl.setNguoiDung(user);
bl.setChuong(chuong);
bl.setTruyen(null); 

binhLuanRepo.save(bl);
}
    public void replyForChuong(Long chuongId,
            Long userId,
            Long parentId,
            String noiDung) {
        Chuong chuong = chuongRepo.findById(chuongId).orElseThrow();
        NguoiDung user = nguoiDungRepo.findById(userId).orElseThrow();
        BinhLuan parent = binhLuanRepo.findById(parentId).orElseThrow();

        BinhLuan reply = new BinhLuan();
        reply.setChuong(chuong);
        reply.setNguoiDung(user);
        reply.setNoiDung(noiDung);
        reply.setParent(parent);

        binhLuanRepo.save(reply);
    				}
    
    
    
    public void reply(Long truyenId,
                      Long userId,
                      Long parentId,
                      String noiDung) {

        Truyen truyen = truyenRepo.findById(truyenId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy truyện"));

        NguoiDung user = nguoiDungRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));

        BinhLuan parent = binhLuanRepo.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy comment cha"));

        BinhLuan reply = new BinhLuan();
        reply.setTruyen(truyen);
        reply.setNguoiDung(user);
        reply.setNoiDung(noiDung);
        reply.setParent(parent);

        binhLuanRepo.save(reply);
    }

   
   
    public void update(Long commentId,String noiDung,Long currentUserId,boolean isAdmin) {

    		BinhLuan bl = binhLuanRepo.findById(commentId).orElseThrow(() -> new RuntimeException("Không tìm thấy bình luận"));

    		Long ownerId = bl.getNguoiDung().getId();

    		if (ownerId.equals(currentUserId)) {bl.setNoiDung(noiDung);binhLuanRepo.save(bl);
    		return; }

    		if (isAdmin) {bl.setNoiDung(noiDung);binhLuanRepo.save(bl);
    		return;}

    		if (bl.getTruyen() != null &&
    				bl.getTruyen().getNguoiDang().getId().equals(currentUserId)) {

    			throw new RuntimeException("Chủ truyện không có quyền sửa bình luận");
    		}
    		
    			throw new RuntimeException("Bạn không có quyền sửa bình luận này");
    			
    				}
    
    
    
    public void updatecmtChuong(Long commentId,String noiDung,Long currentUserId) {

    	BinhLuan bl = binhLuanRepo.findById(commentId).orElseThrow(() -> new RuntimeException("Không tìm thấy bình luận"));

    		if (!bl.getNguoiDung().getId().equals(currentUserId)) {
    			throw new RuntimeException("Bạn không có quyền sửa bình luận này");
    		}

    		bl.setNoiDung(noiDung);
    		binhLuanRepo.save(bl);

    				}
    
    
    
    public void updateReply(Long replyId, String noiDung, Long userId) {

        BinhLuan reply = binhLuanRepo.findById(replyId).orElseThrow(() -> new RuntimeException("Reply not found"));

        if (!reply.getNguoiDung().getId().equals(userId)) {
            throw new RuntimeException("Không có quyền sửa");
        }

        reply.setNoiDung(noiDung);
        binhLuanRepo.save(reply);
    }
    
    public void delete(Long commentId,Long currentUserId, boolean isAdmin) {

    		BinhLuan bl = binhLuanRepo.findById(commentId).orElseThrow(() -> new RuntimeException("Không tìm thấy bình luận"));

    		Long ownerId = bl.getNguoiDung().getId();

    		Long uploaderId = null;

    		if (bl.getTruyen() != null) {uploaderId = bl.getTruyen().getNguoiDang().getId();}
    		if (bl.getChuong() != null) {uploaderId = bl.getChuong().getNguoiDang().getId();}
    		if (!ownerId.equals(currentUserId)
    				&& (uploaderId == null || !uploaderId.equals(currentUserId))
    				&& !isAdmin) {throw new RuntimeException("Bạn không có quyền xóa bình luận này");}

    		binhLuanRepo.delete(bl);
    	}
    
    
    
    public void deletecmtChuong(Long commentId,
            Long currentUserId) {

    	BinhLuan bl = binhLuanRepo.findById(commentId).orElseThrow(() -> new RuntimeException("Không tìm thấy bình luận"));

    	if (!bl.getNguoiDung().getId().equals(currentUserId)) {throw new RuntimeException("Bạn không có quyền xóa bình luận này");}

    		binhLuanRepo.delete(bl);
}
    
    
    
    public void deleteReply(Long replyId, Long userId) {

        BinhLuan reply = binhLuanRepo.findById(replyId).orElseThrow(() -> new RuntimeException("Reply not found"));

        if (!reply.getNguoiDung().getId().equals(userId)) {
            throw new RuntimeException("Không có quyền xóa");
        }

        binhLuanRepo.delete(reply);
    }
    
}