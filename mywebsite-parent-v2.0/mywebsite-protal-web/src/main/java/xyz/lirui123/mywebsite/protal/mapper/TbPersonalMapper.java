package xyz.lirui123.mywebsite.protal.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import xyz.lirui123.mywebsite.pojo.TbPersonal;
import xyz.lirui123.mywebsite.pojo.TbPersonalExample;

import java.util.List;

@Component
public interface TbPersonalMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_personal
     *
     * @mbggenerated
     */
    int countByExample(TbPersonalExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_personal
     *
     * @mbggenerated
     */
    int deleteByExample(TbPersonalExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_personal
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_personal
     *
     * @mbggenerated
     */
    int insert(TbPersonal record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_personal
     *
     * @mbggenerated
     */
    int insertSelective(TbPersonal record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_personal
     *
     * @mbggenerated
     */
    List<TbPersonal> selectByExample(TbPersonalExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_personal
     *
     * @mbggenerated
     */
    TbPersonal selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_personal
     *
     * @mbggenerated
     */
    int updateByExampleSelective(@Param("record") TbPersonal record, @Param("example") TbPersonalExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_personal
     *
     * @mbggenerated
     */
    int updateByExample(@Param("record") TbPersonal record, @Param("example") TbPersonalExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_personal
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(TbPersonal record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_personal
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(TbPersonal record);
}